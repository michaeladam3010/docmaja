#pragma once
#include <QWebEnginePage>
#include <QWebSocketServer>
#include <QWidget>

#include "Settings.h"
#include "HtmlDocument.h"

namespace DocmaPreview {
namespace Internal {

class PreviewPage : public QWebEnginePage
{
public:
    PreviewPage();
    void updateContent(const QString &head, const QString &body);
    void resetContent();
    void highlightLine(int line);
    void scrollToLine(int line);
private:
    void update();

    QString _currentContent;
    bool _skeletonPresent = false;
    QWebSocketServer _webSocketServer;
    QWebSocket* _clientConnection = nullptr;
};

} /*Internal*/
} /*docmala*/
