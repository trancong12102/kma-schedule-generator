# Get current workding directory
SCRIPTPATH=$PWD
# Working directory
JS_DIR="$SCRIPTPATH/assets/js/"
SCSS_DIR="$SCRIPTPATH/assets/scss/"
# Option parameter
parameter=$1
# Colors
BLUE='\033[0;34m'
LIGHT_BLUE='\033[1;34m'
GREEN='\033[0;32m'
NC='\033[0m'

# Building
echo -e "${BLUE}============================================${NC}"
echo -e "${LIGHT_BLUE}BUILDING JAVASCRIPT FILES${NC}"
cd $JS_DIR
npm run build


echo -e "${BLUE}============================================${NC}"
echo -e "${LIGHT_BLUE}BUILDING SCSS FILES${NC}"
cd $SCSS_DIR
sass style.scss ../css/style.css
sass helpers.scss ../css/helpers.css
sass landing-2.scss ../css/landing-2.css


echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}BUILD SUCCESSFUL${NC}"

# Syncing
cd $SCRIPTPATH
cd ../
WEBAPP_DIR="$PWD/src/main/webapp"

echo -e "${BLUE}============================================${NC}"
echo -e "${LIGHT_BLUE}SYNCING INDEX.HTML${NC}"
INDEX_HTML="$SCRIPTPATH/index.html"
INDEX_HTML_DIST="$WEBAPP_DIR/index.html"
rsync -rv $INDEX_HTML $INDEX_HTML_DIST

echo -e "${BLUE}============================================${NC}"
echo -e "${LIGHT_BLUE}SYNCING BOOTSTRAP CSS${NC}"
BOOTSTRAP_CSS="$SCRIPTPATH/assets/bootstrap-4.3.1/dist/css"
BOOTSTRAP_CSS_DIST="$WEBAPP_DIR/assets/bootstrap-4.3.1/dist"
rsync -rv --exclude=**/*.map $BOOTSTRAP_CSS $BOOTSTRAP_CSS_DIST

echo -e "${BLUE}============================================${NC}"
echo -e "${LIGHT_BLUE}SYNCING MAIN JS${NC}"
MAIN_JS="$SCRIPTPATH/assets/js/dist/main.js"
MAIN_JS_DIST="$WEBAPP_DIR/assets/js/dist/main.js"
rsync -rv $MAIN_JS $MAIN_JS_DIST

echo -e "${BLUE}============================================${NC}"
echo -e "${LIGHT_BLUE}SYNCING MAIN CSS${NC}"
MAIN_CSS="$SCRIPTPATH/assets/css"
MAIN_CSS_DIST="$WEBAPP_DIR/assets/css"

STYLE_CSS="$MAIN_CSS/style.css"
STYLE_CSS_DIST="$MAIN_CSS_DIST/style.css"
rsync -rv $STYLE_CSS $STYLE_CSS_DIST

LANDING_2_CSS="$MAIN_CSS/landing-2.css"
LANDING_2_CSS_DIST="$MAIN_CSS_DIST/landing-2.css"
rsync -rv $LANDING_2_CSS $LANDING_2_CSS_DIST

HELPERS_CSS="$MAIN_CSS/helpers.css"
HELPERS_CSS_DIST="$MAIN_CSS_DIST/helpers.css"
rsync -rv $HELPERS_CSS $HELPERS_CSS_DIST

echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}SYNC SUCCESSFUL${NC}"
