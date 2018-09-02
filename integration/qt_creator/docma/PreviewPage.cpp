#include "PreviewPage.h"
#include <QWebSocket>
#include <QTextStream>

namespace DocmaPreview {
namespace Internal {

PreviewPage::PreviewPage()
    : _webSocketServer("docmalaPreviewServer", QWebSocketServer::NonSecureMode)
{
    _webSocketServer.listen();

    connect( &_webSocketServer, &QWebSocketServer::newConnection, [this] {
        _clientConnection = _webSocketServer.nextPendingConnection();
        update();
    });

}

void PreviewPage::updateContent(const QString &head, const QString &body, const QMap<int, QPair<int, QString>> &ids)
{
    _currentContent = body;
    _ids = ids;
    if( !_skeletonPresent ) {
        QString document = "<!doctype html><html><head>";
        document += head + "</head>";

        document += QString( R"foo(
<body>
  <div id="placeholder"></div>
  <script>
    'use strict'
    var placeholder = document.getElementById('placeholder')
    var lastStyle
    var lastElement = null
    var lastLine = -1
    function highlight(id) {
      lastLine = id
      if( lastElement )
        lastElement.style = lastStyle

      var myElement = document.querySelector("#"+id)
      if( myElement ) {
        lastStyle = myElement.style
        myElement.style.border = "2px solid grey"
        myElement.style.borderRadius = "6px"
        myElement.style.background = "lightgrey"
      }
      lastElement = myElement
    }
    function scrollToId(id) {
      var myElement = document.querySelector("#"+id)
      if( myElement ) {
        const elementRect = myElement.getBoundingClientRect()
        const absoluteElementTop = elementRect.top + window.pageYOffset
        const middle = absoluteElementTop - (window.innerHeight / 2)
        window.scrollTo(0, middle)
      }
    }
    var updateText = function(message) {
      placeholder.innerHTML = message.data
      if( typeof(hljs) !== 'undefined' ) {
        var blocks = placeholder.querySelectorAll('pre code')
        var ArrayProto = []
        ArrayProto.forEach.call(blocks, hljs.highlightBlock)
      }
      highlightLine(lastLine)
    }
    var webSocket = new WebSocket("ws://localhost:%1")
    webSocket.onmessage = updateText
  </script>
</body>)foo").arg(_webSocketServer.serverPort());

        setHtml( document, QUrl() );
        _skeletonPresent = true;
    }
    update();
}

void PreviewPage::resetContent()
{
    _skeletonPresent = false;
    _clientConnection->deleteLater();
    _clientConnection = nullptr;
}

void PreviewPage::highlight(int position)
{
    auto id = idAtPosition(position);

    runJavaScript(QString("highlight('%1');").arg(id));
}

void PreviewPage::scrollTo(int position)
{
    auto id = idAtPosition(position);

    runJavaScript(QString("scrollToId('%1');").arg(id));
}

void PreviewPage::update()
{
    if( _clientConnection ) {
        _clientConnection->sendTextMessage(_currentContent);
    }
}

QString PreviewPage::idAtPosition(int position) const
{
    QString id = "dmhl_none";

    auto iter = _ids.upperBound(position);
    if( iter == _ids.begin() || _ids.size() == 0 )
        return id;
    iter--;
    if( iter != _ids.end() && iter->first >= position ) {
        return iter->second;
    }
    return "dmhl_none";
}

void PreviewPage::javaScriptConsoleMessage(QWebEnginePage::JavaScriptConsoleMessageLevel level, const QString &message, int lineNumber, const QString &sourceID)
{
    QTextStream out(stdout);
    out << message << endl;
}

} /*Internal*/
} /*docmala*/
