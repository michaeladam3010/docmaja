package com.docmala.parser.blockParsers;

import com.docmala.Error;
import com.docmala.parser.*;
import com.docmala.plugins.IDocumentPlugin;

import java.util.ArrayDeque;

public class PluginParser implements IBlockParser {
    ArrayDeque<Error> errors = new ArrayDeque<>();
    final ISourceProvider sourceProvider;

    public PluginParser(ISourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    @Override
    public ArrayDeque<Error> errors() {
        return errors;
    }

    @Override
    public boolean tryParse(ISource.Window start, IBlockHolder document) {
        if( start.here().equals('[') && !start.next().equals("[")) {

            final char[] end = {']', ','};

            ParameterParser parameterParser = new ParameterParser();
            ArrayDeque<Parameter> parameters = new ArrayDeque<>();

            while (!start.here().isBlockEnd()) {
                start.skipWhitspaces();

                if( start.here().equals(']') ) {
                    break;
                }
                start.moveForward();

                start = parameterParser.parse(start, end);
                parameters.addLast(parameterParser.parameter());
                errors.addAll(parameterParser.errors());
            }

            if( !start.here().equals(']') ) {
                errors.addLast(new Error(start.here(), "Expected ']'"));
            } else {
                start.moveForward();
            }

            Parameter pluginParameter = parameters.getFirst();

            IDocumentPlugin plugin = null;
            try {
                Class<?> p = null;
                p = Class.forName("com.docmala.plugins.document." + pluginParameter.name());
                if( !IDocumentPlugin.class.isAssignableFrom(p) ) {
                    throw new ClassNotFoundException();
                }
                plugin = (IDocumentPlugin)p.newInstance();
            } catch (ClassNotFoundException e) {
                errors.addLast(new Error(start.here(), "Plugin '" + pluginParameter.name() + "' not found."));
                return true;
            } catch (IllegalAccessException e) {
                errors.addLast(new Error(start.here(), "Plugin '" + pluginParameter.name() + "' cannot be accessed."));
                return true;
            } catch (InstantiationException e) {
                errors.addLast(new Error(start.here(), "Plugin '" + pluginParameter.name() + "' cannot be instanciated."));
                return true;
            }

            plugin.process(parameters, (Document)document, sourceProvider);
            errors.addAll(plugin.errors());

            return true;
        }
        return false;
    }
}
