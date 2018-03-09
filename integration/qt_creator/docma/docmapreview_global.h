#pragma once

#include <QtGlobal>

#if defined(DOCMAPREVIEW_LIBRARY)
#  define DOCMAPREVIEWSHARED_EXPORT Q_DECL_EXPORT
#else
#  define DOCMAPREVIEWSHARED_EXPORT Q_DECL_IMPORT
#endif
