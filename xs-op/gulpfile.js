const {series, parallel, src, dest, watch, task} = require('gulp');

var options = require('minimist')(process.argv.slice(2));
console.log(options);

var plumber = require('gulp-plumber');
var gulpif = require('gulp-if');
var concat = require('gulp-concat');
var rename = require('gulp-rename');

var babel = require('gulp-babel');
var uglify = require('gulp-uglify');


var sass = require('gulp-sass');
var less = require('gulp-less');
var autoprefixer = require('gulp-autoprefixer');
var minifyCss = require('gulp-minify-css');


function vendor(cb) {
    src([
        './src/main/webapp/vendor/reqwest.min.js',
        './src/main/webapp/vendor/react/react.min.js',
        './src/main/webapp/vendor/react/react-dom.min.js',
    ])
        .pipe(plumber())
        .pipe(concat('vendor.js'))
        .pipe(dest('./src/main/webapp/build/js/'));
    cb();
}

function js(cb) {
    src([
        './src/main/webapp/static/js/base/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('base.js'))
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0'],
            plugins: ["transform-class-properties"],
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(dest('./src/main/webapp/build/js/'));

    src([
        './src/main/webapp/static/js/*.js',
    ])
        .pipe(plumber())
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0'],
            plugins: ["transform-class-properties"],
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(dest('./src/main/webapp/build/js/'));

    src([
        './src/main/webapp/static/components/base/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('base.js'))
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0'],
            plugins: ["transform-class-properties"],
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(dest('./src/main/webapp/build/components/'));

    src([
        './src/main/webapp/static/components/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('components.js'))
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0'],
            plugins: ["transform-class-properties"],
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(dest('./src/main/webapp/build/components/'));
    cb();
}

function css(cb) {
    src([
        './src/main/webapp/static/**/*.css',
    ])
        .pipe(plumber())
        .pipe(autoprefixer({
            browsers: ['last 2 versions'],
            cascade: false
        }))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(dest('./src/main/webapp/build'));

    src([
        './src/main/webapp/static/**/*.less',
    ])
        .pipe(plumber())
        .pipe(less())
        .pipe(autoprefixer({
            browsers: ['last 2 versions'],
            cascade: false
        }))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(dest('./src/main/webapp/build'));

    src([
        './src/main/webapp/static/**/*.scss',
    ])
        .pipe(plumber())
        .pipe(sass())
        .pipe(autoprefixer({
            browsers: ['last 2 versions'],
            cascade: false
        }))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(dest('./src/main/webapp/build'));
    cb();
}


exports.vendor = vendor;
exports.css = css;
exports.js = js;

exports.default = series(vendor, css, js, function (cb) {
    watch('./src/main/webapp/static/**/*.js', js);
    watch('./src/main/webapp/static/**/*.css', css);
    watch('./src/main/webapp/static/**/*.less', css);
    watch('./src/main/webapp/static/**/*.scss', css);
    cb();
});

