package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.plugins.IDocumentPlugin;
import com.docmala.plugins.document.Plugin;
import org.reflections.Reflections;

import java.util.ArrayDeque;
import java.util.Set;

public class PluginParser implements IBlockParser {
    final ISourceProvider sourceProvider;
    final StringBuilder block = new StringBuilder();
    DataBlock dataBlock;
    ArrayDeque<Error> errors = new ArrayDeque<>();
    Set<Class<? extends IDocumentPlugin>> plugins = new Reflections("com.docmala.plugins.document").getSubTypesOf(IDocumentPlugin.class);

    public PluginParser(ISourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    boolean parseBlock(ISource.Window start) {
        block.setLength(0);
        ISource.Window begin = start.copy();
        start.skipWhitespaces();
        DataBlock.Builder dataBlockBuilder = new DataBlock.Builder();
        if (start.here().isNewLine()) {
            start.moveForward();
            int level = 0;
            while (start.here().equals('-')) {
                level++;
                start.moveForward();
            }

            if( level < 2 ) {
                start.setTo(begin);
                return false;
            }
            while (!start.here().isBlockEnd()) {
                start.moveForward();
            }
            start.moveForward();

            dataBlockBuilder.setPosition(start.here());

            while (!start.here().isEof()) {
                int endLevel = 0;

                if (start.here().equals('-')) {
                    while (start.here().equals('-')) {
                        endLevel++;
                        start.moveForward();
                    }

                    if (endLevel >= level) {
                        start.skipWhitespaces();
                        dataBlockBuilder.setData(block.toString());
                        dataBlock = dataBlockBuilder.build();
                        return true;
                    } else {
                        for (int i = 0; i < endLevel; i++) {
                            block.append('-');
                        }
                        block.append(start.here().get());
                        start.moveForward();
                        continue;
                    }
                }
                block.append(start.here().get());
                start.moveForward();
            }
            return false;
        }
        return false;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        if (start.here().equals('[') && !start.next().equals('[') || Character.isUpperCase(start.here().get())) {
            errors.clear();
            ArrayDeque<Parameter> parameters = new ArrayDeque<>();
            ISource.Position begin = start.here();
            dataBlock = null;
            if( start.here().equals('[') ) {
                final char[] end = {']', ','};

                ParameterParser parameterParser = new ParameterParser();

                while (!start.here().isBlockEnd()) {
                    start.skipWhitespaces();

                    if (start.here().equals(']')) {
                        break;
                    }
                    start.moveForward();

                    start = parameterParser.parse(start, end);
                    parameters.addLast(parameterParser.parameter());
                    errors.addAll(parameterParser.errors());
                }

                if (!start.here().equals(']')) {
                    errors.addLast(new Error(start.here(), "Expected ']'"));
                } else {
                    start.moveForward();
                }
            } else {
                ISource.Window startWindow = start.copy();
                final char[] end = {':'};
                final char[] paramEnd = {};

                ParameterParser parameterParser = new ParameterParser();
                // parse plugin name
                start = parameterParser.parse(start, end);
                parameters.addLast(parameterParser.parameter());
                errors.addAll(parameterParser.errors());

                if( start.here().equals(':')) {
                    start.moveForward();

                    // parse default parameter
                    start = parameterParser.parse(start, paramEnd, true);
                    parameters.addLast(parameterParser.parameter());
                    errors.addAll(parameterParser.errors());

                    String potentialPlugin = parameters.getFirst().name();
                    if (!potentialPlugin.toUpperCase().equals(potentialPlugin)) {
                        start.setTo(startWindow);
                        return false;
                    }

                    if (parameters.getLast().value().isEmpty()) {
                        errors.addLast(new Error(parameters.getLast().position(), "Parameter missing. (PLUGIN: parameter)"));
                        return true;
                    }
                } else {
                    start.setTo(startWindow);
                    return false;
                }
            }


            Parameter pluginParameter = parameters.getFirst();

            IDocumentPlugin plugin = null;
            try {
                Class<? extends IDocumentPlugin> p = null;
                outerloop: for(Class<? extends IDocumentPlugin> dp : plugins)
                    for ( Plugin a : dp.getAnnotationsByType(Plugin.class) )
                        if(a.value().equalsIgnoreCase(pluginParameter.name())) {
                            p = dp;

                            String[] args = a.defaultParameters().split(" ");
                            for(String arg : args) {
                                String[] argSplitted = arg.split("=");
                                if(argSplitted.length != 2)
                                    continue;

                                boolean found = false;
                                for(Parameter params : parameters) {
                                    if(params.name().equalsIgnoreCase(argSplitted[0])) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found)
                                 parameters.addLast(new Parameter(argSplitted[0], argSplitted[1], null, null));
                            }
                            break outerloop;
                        }

                if (p == null) {
                    throw new ClassNotFoundException();
                }

                plugin = p.newInstance();
            } catch (ClassNotFoundException e) {
                errors.add(new Error(start.here(), "Plugin '" + pluginParameter.name() + "' not found."));
                return true;
            } catch (IllegalAccessException e) {
                errors.add(new Error(start.here(), "Plugin '" + pluginParameter.name() + "' cannot be accessed."));
                return true;
            } catch (InstantiationException e) {
                errors.add(new Error(start.here(), "Plugin '" + pluginParameter.name() + "' cannot be instantiated."));
                return true;
            }
            IDocumentPlugin.BlockProcessing blockProcessing = plugin.blockProcessing();

            if (blockProcessing != IDocumentPlugin.BlockProcessing.No) {
                if (!parseBlock(start)) {
                    if (blockProcessing == IDocumentPlugin.BlockProcessing.Required) {
                        errors.add(new Error(start.here(), "Plugin '" + pluginParameter.name() + "' requires a data block ('--')."));
                    }
                }
            }

            String defaultParameter = plugin.defaultParameter();
            if( defaultParameter != null ) {
                ArrayDeque<Parameter> newParameters = new ArrayDeque<>();

                for (Parameter parameter : parameters) {
                    if (parameter.name().isEmpty()) {
                        Parameter.Builder parameterBuilder = new Parameter.Builder();
                        parameterBuilder.setName(defaultParameter);
                        parameterBuilder.setPosition(parameter.position());
                        parameterBuilder.setValue(parameter.value());
                        parameterBuilder.setValuePosition(parameter.valuePosition());
                        newParameters.addLast(parameterBuilder.build());
                    } else {
                        newParameters.addLast(parameter);
                    }
                }
                parameters = newParameters;
            }

            plugin.process(begin, start.here(), parameters, dataBlock, (Document) document, sourceProvider);
            errors.addAll(plugin.errors());

            return true;
        }
        return false;
    }
}
