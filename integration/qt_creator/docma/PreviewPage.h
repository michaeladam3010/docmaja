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
    void updateContent(const QString &head, const QString &body, const QMap<int, QPair<int, QString>> &ids);
    void resetContent();
    void highlight(int position);
    void scrollTo(int position);

private:
    void update();

    QString idAtPosition(int position) const;

    QString _currentContent;
    QMap<int, QPair<int, QString>> _ids;
    bool _skeletonPresent = false;
    QWebSocketServer _webSocketServer;
    QWebSocket* _clientConnection = nullptr;

    // QWebEnginePage interface
protected:
    void javaScriptConsoleMessage(JavaScriptConsoleMessageLevel level, const QString &message, int lineNumber, const QString &sourceID);
};

} /*Internal*/
} /*docmala*/
