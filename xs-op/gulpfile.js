var gulp = require('gulp');

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


gulp.task('vendor', function () {
    gulp.src([
        './src/main/webapp/vendor/reqwest.min.js',
        './src/main/webapp/vendor/react/react.min.js',
        './src/main/webapp/vendor/react/react-dom.min.js',
    ])
        .pipe(plumber())
        .pipe(concat('vendor.js'))
        .pipe(gulp.dest('./src/main/webapp/build/js/'));

});

gulp.task('js', function () {
    gulp.src([
        './src/main/webapp/static/js/base/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('base.js'))
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0']
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(gulp.dest('./src/main/webapp/build/js/'));

    gulp.src([
        './src/main/webapp/static/js/*.js',
    ])
        .pipe(plumber())
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0']
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(gulp.dest('./src/main/webapp/build/js/'));

    gulp.src([
        './src/main/webapp/static/components/base/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('base.js'))
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0']
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(gulp.dest('./src/main/webapp/build/components/'));

    gulp.src([
        './src/main/webapp/static/components/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('components.js'))
        .pipe(babel({
            presets: ['es2015', 'react', 'stage-0']
        }))
        .pipe(gulpif(options.build, uglify()))
        .pipe(gulp.dest('./src/main/webapp/build/components/'));

});
gulp.task('css', function () {
    gulp.src([
        './src/main/webapp/static/**/*.css',
    ])
        .pipe(plumber())
        .pipe(autoprefixer())
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(gulp.dest('./src/main/webapp/build'));

    gulp.src([
        './src/main/webapp/static/**/*.less',
    ])
        .pipe(plumber())
        .pipe(less())
        .pipe(autoprefixer())
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(gulp.dest('./src/main/webapp/build'));

    gulp.src([
        './src/main/webapp/static/**/*.scss',
    ])
        .pipe(plumber())
        .pipe(sass())
        .pipe(autoprefixer())
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(gulp.dest('./src/main/webapp/build'));
});

gulp.task('default', ['vendor', 'css', 'js'], function () {
    if (!options.build) {
        gulp.watch('./src/main/webapp/static/**/*.js', ['js']);
        gulp.watch('./src/main/webapp/static/**/*.css', ['css']);
        gulp.watch('./src/main/webapp/static/**/*.less', ['css']);
        gulp.watch('./src/main/webapp/static/**/*.scss', ['css']);
    }
});
