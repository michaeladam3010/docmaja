#pragma once

#include "DocmaClient.h"
#include "PreviewPage.h"
#include "PreviewPane.h"
#include "docmapreview_global.h"

#include <coreplugin/idocument.h>
#include <extensionsystem/iplugin.h>

namespace DocmaPreview {
namespace Internal {

class DocmaPreviewPlugin : public ExtensionSystem::IPlugin
{
    Q_OBJECT
    Q_PLUGIN_METADATA(IID "org.qt-project.Qt.QtCreatorPlugin" FILE "DocmaPreview.json")

public:
    DocmaPreviewPlugin();
    ~DocmaPreviewPlugin();

    bool initialize(const QStringList &arguments, QString *errorString);
    void extensionsInitialized();
    ShutdownFlag aboutToShutdown();

    void showPreview(bool show);

private:
    void currentEditorChanged();

    void documentChanged();
    void updatePreview();
    void updateHighlight();

    void triggerAction();
    bool _paneWasVisible {false};

    Settings _settings;

    PreviewPane* _previewPane = nullptr;
    PreviewPage* _previewPage = nullptr;

    DocmaClient _docmaClient;
    Core::IDocument* _document = nullptr;
    QMetaObject::Connection _documentChangedConnection;

};

} // namespace Internal
} // namespace DocmaPreview
