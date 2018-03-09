#include "Settings.h"
#include "docmapreviewconstants.h"

#include <coreplugin/icore.h>

#include <QSettings>

void Settings::save() const
{
    auto settings = Core::ICore::settings();
    settings->beginGroup(QLatin1String(DocmaPreview::Constants::SETTINGS_GROUP));
    settings->setValue(QLatin1String(DocmaPreview::Constants::HIGHLIGHT_CURRENT_LINE), _highlightCurrentLine);
    settings->setValue(QLatin1String(DocmaPreview::Constants::FOLLOW_CURSOR), _followCursor);
    settings->setValue(QLatin1String(DocmaPreview::Constants::ZOOM), _zoom);
    settings->setValue(QLatin1String(DocmaPreview::Constants::PREVIEW_SHOWN), _previewShown);

    settings->endGroup();
    settings->sync();
}

void Settings::load()
{
    auto settings = Core::ICore::settings();
    settings->beginGroup(QLatin1String(DocmaPreview::Constants::SETTINGS_GROUP));
    _highlightCurrentLine = settings->value(QLatin1String(DocmaPreview::Constants::HIGHLIGHT_CURRENT_LINE), true).toBool();
    _followCursor = settings->value(QLatin1String(DocmaPreview::Constants::FOLLOW_CURSOR), true).toBool();
    _zoom = settings->value(QLatin1String(DocmaPreview::Constants::ZOOM), 1.0).toDouble();
    _previewShown = settings->value(QLatin1String(DocmaPreview::Constants::PREVIEW_SHOWN), true).toBool();
    settings->endGroup();
}

bool Settings::highlightCurrentLine() const
{
    return _highlightCurrentLine;
}

void Settings::setHighlightCurrentLine(bool highlightCurrentLine)
{
    _highlightCurrentLine = highlightCurrentLine;
}

bool Settings::followCursor() const
{
    return _followCursor;
}

void Settings::setFollowCursor(bool followCursor)
{
    _followCursor = followCursor;
}

double Settings::zoom() const
{
    return _zoom;
}

void Settings::setZoom(double zoom)
{
    _zoom = zoom;
}

bool Settings::previewShown() const
{
    return _previewShown;
}

void Settings::setPreviewShown(bool previewShown)
{
    _previewShown = previewShown;
}
