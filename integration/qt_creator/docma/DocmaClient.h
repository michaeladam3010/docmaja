#pragma once

#include <QtWebSockets/QtWebSockets>

struct IDInfo {
    int start;
    int end;
    QString id;
};

class DocmaClient : public QObject
{
    Q_OBJECT
public:
    DocmaClient();

    void renderDocument(const QByteArray &fileContent, const QString &fileName);

signals:
    void documentRendered(const QString &head, const QString &body, const QMap<int, QPair<int, QString>> &ids);

private slots:
    void onTextMessageReceived(const QString &message);
    void onConnected();
    void onDisconnected();

private:

    void remoteGetFile(const QString &fileName, int resultId);

    QMap<int, QPair<int, QString>> getIDs(const QJsonValue &idNode) const;

    QWebSocket m_webSocket;
    QString _currentFileName;
    QByteArray _currentFileContent;
    int _requestId {0};
};
