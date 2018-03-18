#include "DocmaPreviewPlugin.h"
#include "docmapreviewconstants.h"

#include <coreplugin/icore.h>
#include <coreplugin/icontext.h>
#include <coreplugin/actionmanager/actionmanager.h>
#include <coreplugin/actionmanager/command.h>
#include <coreplugin/actionmanager/actioncontainer.h>
#include <coreplugin/coreconstants.h>
#include <coreplugin/editormanager/editormanager.h>

#include <coreplugin/rightpane.h>

#include <projectexplorer/taskhub.h>

#include <texteditor/texteditor.h>

#include <QAction>
#include <QMessageBox>
#include <QMainWindow>
#include <QMenu>
#include <QPlainTextEdit>

namespace DocmaPreview {
namespace Internal {

DocmaPreviewPlugin::DocmaPreviewPlugin()
{
    // Create your members
}

DocmaPreviewPlugin::~DocmaPreviewPlugin()
{
    // Unregister objects from the plugin manager's object pool
    // Delete members
}

bool DocmaPreviewPlugin::initialize(const QStringList &arguments, QString *errorString)
{
    // Register objects in the plugin manager's object pool
    // Load settings
    _settings.load();

    // Add actions to menus
    // Connect to other plugins' signals
    // In the initialize function, a plugin can be sure that the plugins it
    // depends on have initialized their members.

    Q_UNUSED(arguments)
    Q_UNUSED(errorString)

    ProjectExplorer::TaskHub::addCategory(Constants::TASK_ID, "Documentation errors", true);

    auto action = new QAction(tr("DocmaPreview Action"), this);
    Core::Command *cmd = Core::ActionManager::registerAction(action, Constants::ACTION_ID,
                                                             Core::Context(Core::Constants::C_GLOBAL));
    cmd->setDefaultKeySequence(QKeySequence(tr("Ctrl+Alt+x")));
    connect(action, &QAction::triggered, [this]() {
        showPreview(!Core::RightPaneWidget::instance()->isShown());
    });


    Core::ActionContainer *menu = Core::ActionManager::createMenu(Constants::MENU_ID);
    menu->menu()->setTitle(tr("DocmaPreview"));
    menu->addAction(cmd);
    Core::ActionManager::actionContainer(Core::Constants::M_TOOLS)->addMenu(menu);

    connect(Core::EditorManager::EditorManager::instance(),
            &Core::EditorManager::EditorManager::currentEditorChanged,
            this,
            &DocmaPreviewPlugin::currentEditorChanged );

    _previewPane = new PreviewPane(&_settings);
    connect( _previewPane, &PreviewPane::hide, [this] {
        showPreview(false);
    });

    connect( _previewPane, &PreviewPane::highlightCurrentLineChanged, [this] {
        updateHighlight();
    });

    connect( _previewPane, &PreviewPane::followCursorChanged, [this] {
        updateHighlight();
    });

    _previewPage = new PreviewPage();
    _previewPane->setPage(_previewPage);

    _paneWasVisible = _settings.previewShown();

    connect(&_docmaClient, &DocmaClient::documentRendered, _previewPage, &PreviewPage::updateContent );

    Core::RightPaneWidget::instance()->setWidget(_previewPane);

    return true;
}

void DocmaPreviewPlugin::extensionsInitialized()
{
    // Retrieve objects from the plugin manager's object pool
    // In the extensionsInitialized function, a plugin can be sure that all
    // plugins that depend on it are completely initialized.
}

ExtensionSystem::IPlugin::ShutdownFlag DocmaPreviewPlugin::aboutToShutdown()
{
    // Save settings
    // Disconnect from signals that are not needed during shutdown
    // Hide UI (if you add UI that is not in the main window directly)

    _settings.setPreviewShown( Core::RightPaneWidget::instance()->isShown() );

    _settings.save();
    return SynchronousShutdown;
}

void DocmaPreviewPlugin::showPreview(bool show)
{
    if( show ) {
        updatePreview();
    }
    Core::RightPaneWidget::instance()->setShown(show);
}

void DocmaPreviewPlugin::currentEditorChanged()
{
    auto document = Core::EditorManager::instance()->currentDocument();
    disconnect(_documentChangedConnection);
    if( document ) {
        _document = document;
        _documentChangedConnection = connect( document, &Core::IDocument::contentsChanged, this, &DocmaPreviewPlugin::updatePreview );
        documentChanged();
    }

    auto editor = TextEditor::BaseTextEditor::currentTextEditor();
    if( editor != nullptr ) {
         connect( editor->editorWidget(), &QPlainTextEdit::cursorPositionChanged, this, &DocmaPreviewPlugin::updateHighlight);
    }
}

void DocmaPreviewPlugin::documentChanged()
{
    if( !_document || !_document->filePath().endsWith(".dom") ) {
        _paneWasVisible = Core::RightPaneWidget::instance()->isShown();
        showPreview(false);
        return;
    } else {
        showPreview(_paneWasVisible);
    }

    if( !Core::RightPaneWidget::instance()->isShown() )
        return;

    updatePreview();
}

void DocmaPreviewPlugin::updatePreview()
{
    if( _document ) {
        _docmaClient.renderDocument(_document->contents(), _document->filePath().toString());
    }
}

void DocmaPreviewPlugin::updateHighlight()
{
    auto editor = TextEditor::BaseTextEditor::currentTextEditor();
    if( editor ) {
        if( _settings.highlightCurrentLine() ) {
           _previewPage->highlightLine(editor->editorWidget()->textCursor().blockNumber()+1);
        } else {
            _previewPage->highlightLine(-1);
        }

        if( _settings.followCursor() ) {
            _previewPage->scrollToLine(editor->editorWidget()->textCursor().blockNumber()+1);
        }
    }
}

void DocmaPreviewPlugin::triggerAction()
{
    QMessageBox::information(Core::ICore::mainWindow(),
                             tr("Action Triggered"),
                             tr("This is an action from DocmaPreview."));
}

} // namespace Internal
} // namespace DocmaPreview
