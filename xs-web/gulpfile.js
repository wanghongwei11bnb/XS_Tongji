const options = require('minimist')(process.argv.slice(2));
console.log(options);
const {series, parallel, src, dest, watch, task} = require('gulp');
const sass = require('gulp-sass');
const autoprefixer = require('gulp-autoprefixer');
const minifyCss = require('gulp-minify-css');
const babel = require("gulp-babel");
const gulpif = require("gulp-if");
const uglify = require('gulp-uglify');
const concat = require('gulp-concat');
const connect = require('gulp-connect');
let rename = require('gulp-rename');

const imagemin = require('gulp-imagemin');

const plumber = require('gulp-plumber');

const input = {
    path: `./src/main/resources/static`,
};


const output = {
    path: `./src/main/resources/static/build`,
};


console.log(options);

function copy(cb) {
    // js-cookie
    src([
        './node_modules/js-cookie/src/js.cookie.js',
    ])
        .pipe(plumber())
        .pipe(dest('./src/main/webapp/build/'));
    // jquery
    src([
        './node_modules/jquery/dist/jquery.min.js',
    ])
        .pipe(plumber())
        .pipe(dest('./src/main/webapp/build/'));
    // bootstrap
    src([
        './node_modules/bootstrap/dist/**/*',
    ])
        .pipe(plumber())
        .pipe(dest('./src/main/webapp/build/bootstrap/'));
    // swiper
    src([
        './node_modules/swiper/dist/**/*',
    ])
        .pipe(plumber())
        .pipe(dest('./src/main/webapp/build/swiper/'));
    // animate.css
    src([
        './node_modules/animate.css/animate.min.css',
    ])
        .pipe(plumber())
        .pipe(dest('./src/main/webapp/build/'));
    if (cb) cb();
}


function js(cb) {
    // base.js
    src([
        './src/main/webapp/static/js/base/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('base.js'))
        .pipe(babel({presets: ['es2015', 'stage-0']}))
        .pipe(gulpif(options.build, uglify()))
        .pipe(dest('./src/main/webapp/build/'));

    // *.js
    src([
        './src/main/webapp/static/js/*.js',
    ])
        .pipe(plumber())
        .pipe(babel({presets: ['es2015', 'react', 'stage-0']}))
        .pipe(gulpif(options.build, uglify()))
        .pipe(dest('./src/main/webapp/build/js/'));
    if (cb) cb();

}

function css(cb) {
    src([
        './src/main/webapp/static/**/*.css',
    ])
        .pipe(plumber())
        .pipe(autoprefixer({browsers: ['last 2 versions'], cascade: false}))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(dest('./src/main/webapp/build'));

    src([
        './src/main/webapp/static/**/*.less',
    ])
        .pipe(plumber())
        .pipe(less())
        .pipe(autoprefixer({browsers: ['last 2 versions'], cascade: false}))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(dest('./src/main/webapp/build'));

    src([
        './src/main/webapp/static/**/*.scss',
        '!_define.scss',
    ])
        .pipe(plumber())
        .pipe(sass())
        .pipe(autoprefixer({browsers: ['last 2 versions'], cascade: false}))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(dest('./src/main/webapp/build'));
    if (cb) cb();
}


exports.copy = copy;
exports.js = js;
exports.css = css;


exports.default = series(
    copy,
    js,
    css,
    function (cb) {
        if (!options.build) {
            watch('./src/main/webapp/static/js/**/*', ['js']);
            watch('./src/main/webapp/static/css/**/*', ['css']);
        }
        if (cb) cb();
    }
);
