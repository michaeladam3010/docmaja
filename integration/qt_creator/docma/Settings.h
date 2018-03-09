#pragma once

#include <QtGlobal>
#include <QDir>

QT_FORWARD_DECLARE_CLASS(QSettings)

class Settings
{
public:
    void save() const;
    void load();

    bool highlightCurrentLine() const;
    void setHighlightCurrentLine(bool highlightCurrentLine);

    bool followCursor() const;
    void setFollowCursor(bool followCursor);

    double zoom() const;
    void setZoom(double zoom);

    bool previewShown() const;
    void setPreviewShown(bool previewShown);

private:
    bool _highlightCurrentLine {true};
    bool _followCursor {true};
    bool _previewShown {true};
    double _zoom = 1;
};
