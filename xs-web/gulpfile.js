let gulp = require('gulp');
let options = require('minimist')(process.argv.slice(2));

let plumber = require('gulp-plumber');
let gulpif = require('gulp-if');
let concat = require('gulp-concat');
let rename = require('gulp-rename');

let babel = require('gulp-babel');
let uglify = require('gulp-uglify');

let sass = require('gulp-sass');
let less = require('gulp-less');
let autoprefixer = require('gulp-autoprefixer');
let minifyCss = require('gulp-minify-css');


console.log(options);

gulp.task('copy', function () {
    // js-cookie
    gulp.src([
        './node_modules/js-cookie/src/js.cookie.js',
    ])
        .pipe(plumber())
        .pipe(gulp.dest('./src/main/webapp/build/'));
    // jquery
    gulp.src([
        './node_modules/jquery/dist/jquery.min.js',
    ])
        .pipe(plumber())
        .pipe(gulp.dest('./src/main/webapp/build/'));
    // bootstrap
    gulp.src([
        './node_modules/bootstrap/dist/**/*',
    ])
        .pipe(plumber())
        .pipe(gulp.dest('./src/main/webapp/build/bootstrap/'));
    // swiper
    gulp.src([
        './node_modules/swiper/dist/**/*',
    ])
        .pipe(plumber())
        .pipe(gulp.dest('./src/main/webapp/build/swiper/'));
    // animate.css
    gulp.src([
        './node_modules/animate.css/animate.min.css',
    ])
        .pipe(plumber())
        .pipe(gulp.dest('./src/main/webapp/build/'));

});


gulp.task('js', function () {
    // base.js
    gulp.src([
        './src/main/webapp/static/js/base/*.js',
    ])
        .pipe(plumber())
        .pipe(concat('base.js'))
        .pipe(babel({presets: ['es2015', 'stage-0']}))
        .pipe(gulpif(options.build, uglify()))
        .pipe(gulp.dest('./src/main/webapp/build/'));

    // *.js
    gulp.src([
        './src/main/webapp/static/js/*.js',
    ])
        .pipe(plumber())
        .pipe(babel({presets: ['es2015', 'react', 'stage-0']}))
        .pipe(gulpif(options.build, uglify()))
        .pipe(gulp.dest('./src/main/webapp/build/js/'));


});
gulp.task('css', function () {
    gulp.src([
        './src/main/webapp/static/**/*.css',
    ])
        .pipe(plumber())
        .pipe(autoprefixer({browsers: ['last 2 versions'], cascade: false}))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(gulp.dest('./src/main/webapp/build'));

    gulp.src([
        './src/main/webapp/static/**/*.less',
    ])
        .pipe(plumber())
        .pipe(less())
        .pipe(autoprefixer({browsers: ['last 2 versions'], cascade: false}))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(gulp.dest('./src/main/webapp/build'));

    gulp.src([
        './src/main/webapp/static/**/*.scss',
    ])
        .pipe(plumber())
        .pipe(sass())
        .pipe(autoprefixer({browsers: ['last 2 versions'], cascade: false}))
        .pipe(gulpif(options.build, minifyCss()))
        .pipe(gulp.dest('./src/main/webapp/build'));
});

gulp.task('default', ['copy', 'css', 'js'], function () {
    if (!options.build) {
        gulp.watch('./src/main/webapp/static/js/**/*', ['js']);
        gulp.watch('./src/main/webapp/static/css/**/*', ['css']);
    }
});
