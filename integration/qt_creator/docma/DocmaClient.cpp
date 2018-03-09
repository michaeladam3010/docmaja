#include "DocmaClient.h"
#include "docmapreviewconstants.h"

#include <coreplugin/editormanager/documentmodel.h>
#include <coreplugin/idocument.h>
#include <projectexplorer/taskhub.h>

#include <QDebug>

DocmaClient::DocmaClient()
{
    connect(&m_webSocket, &QWebSocket::connected, this, &DocmaClient::onConnected);
    connect(&m_webSocket, &QWebSocket::disconnected, this, &DocmaClient::onDisconnected);
    connect(&m_webSocket, &QWebSocket::textMessageReceived, this, &DocmaClient::onTextMessageReceived);
    m_webSocket.open(QUrl("ws://localhost:47294"));
}

void DocmaClient::renderDocument(const QByteArray &fileContent, const QString &fileName)
{
    if( !m_webSocket.isValid() ) {
        m_webSocket.open(QUrl("ws://localhost:47294"));
        return;
    }

    QJsonObject obj {
        { "jsonrpc", "2.0" },
        { "method", "render"},
        { "params",
            QJsonObject {
                { "filename", fileName },
            }
        },
        {"id", _requestId}
    };

    _requestId++;
    _currentFileContent = fileContent;
    _currentFileName = fileName;

    QString msg = QString::fromLatin1(QJsonDocument(obj).toJson(QJsonDocument::Compact));
    m_webSocket.sendTextMessage(msg);
}

void DocmaClient::onTextMessageReceived(const QString &message)
{
    QJsonDocument msg = QJsonDocument::fromJson(message.toLatin1());

    if( msg["method"] != QJsonValue::Undefined ) {
        QJsonValue method = msg["method"];
        if( method == "getFile" ) {
            remoteGetFile(msg["params"]["fileName"].toString(), msg["id"].toInt());
        }
    } else if( msg["result"] != QJsonValue::Undefined ){
        QJsonValue result = msg["result"];
        if( result["head"] != QJsonValue::Undefined && result["body"] != QJsonValue::Undefined ) {
            emit documentRendered(result["head"].toString(), result["body"].toString());
        }
    } else if( msg["error"] != QJsonValue::Undefined ){
        if( msg["error"] != QJsonValue::Undefined && msg["error"]["code"].toInt() == -231 ) {
            ProjectExplorer::TaskHub::clearTasks(DocmaPreview::Constants::TASK_ID);
            auto errors = msg["error"]["data"].toArray();
            for( int i = 0; i < errors.size(); i++ ) {
                const QJsonValue error = errors[i];
                ProjectExplorer::TaskHub::addTask(ProjectExplorer::Task(
                                                      ProjectExplorer::Task::Error,
                                                      error["message"].toString(),
                                                      Utils::FileName::fromString(error["file"].toString()),
                                                      error["line"].toInt(),
                                                      DocmaPreview::Constants::TASK_ID));
            }
        }
    }
}

void DocmaClient::onConnected()
{
//    renderDocument("asdf", "test");
}

void DocmaClient::onDisconnected()
{
    m_webSocket.open(QUrl("ws://localhost:47294"));
}

void DocmaClient::remoteGetFile(const QString &fileName, int resultId)
{
    QByteArray fileContent;

    for( const auto &doc : Core::DocumentModel::openedDocuments() ) {
        QString docFileName = doc->filePath().fileName(-1);
        if( fileName == docFileName ) {
            fileContent = doc->contents();
        }
    }

    QJsonObject obj {
        { "jsonrpc", "2.0" },
        { "result",
            QJsonObject {
                { "data", QString::fromLatin1(fileContent.toBase64()) }
            }
        },
        {"id", resultId}
    };

    QString msg = QString::fromLatin1(QJsonDocument(obj).toJson(QJsonDocument::Compact));
    m_webSocket.sendTextMessage(msg);
}
