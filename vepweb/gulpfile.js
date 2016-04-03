var gulp = require("gulp");
var gutil = require('gulp-util');
var watch = require('gulp-watch');
var plumber = require('gulp-plumber');
var babel = require("gulp-babel");
var webserver = require('gulp-webserver');
var uglify = require('gulp-uglify');
var minifyInline = require('gulp-minify-inline');
var htmlmin = require('gulp-htmlmin');
var inlinesource = require('gulp-inline-source');

/// Constants
var tasks = {
  devHTML: "devHTML",
  devJS: "devJS",
  dev: "dev",
  buildHTML: "buildHTML",
  buildJS: "buildJS",
  build: "build",
  run: "run"
};

var paths = {
  html: "src/**/*.html",
  js: "src/**/*.es6",
  dist: "dist",
  distHTML: "dist/** / *.html"
};

/// Helpers
function handleError(e) {
  gutil.log(gutil.colors.red(e));
  this.emit("end");
}

/// Dev tasks
gulp.task(tasks.devJS, function () {
  return gulp.src(paths.js)
    .pipe(babel())
    .on('error', handleError)
    .pipe(gulp.dest(paths.dist));
});

gulp.task(tasks.devHTML, function () {
  return gulp.src(paths.html)
    .on('error', handleError)
    .pipe(gulp.dest(paths.dist));
});

gulp.task(tasks.dev, [tasks.devJS, tasks.devHTML], function () {
  gulp.watch(paths.js, [tasks.devJS]);
  gulp.watch(paths.html, [tasks.devHTML]);

  gulp.src(".")
    .pipe(webserver({
      livereload: true,
      fallback: "index.html"
    }));
});

/// Prod tasks
gulp.task(tasks.buildHTML, function () {
  return gulp.src(paths.html)
    .pipe(minifyInline())
    .pipe(htmlmin({collapseWhitespace: true}))
    .pipe(gulp.dest(paths.dist));
});

gulp.task(tasks.buildJS, function () {
  return gulp.src(paths.js)
    .pipe(babel())
    .pipe(uglify())
    .pipe(gulp.dest(paths.dist));
});

gulp.task(tasks.build, [tasks.buildHTML, tasks.buildJS], function () {
  return gulp.src(paths.distHTML)
    .pipe(inlinesource())
    .pipe(gulp.dest(paths.dist));
});

gulp.task(tasks.run, [tasks.build], function () {
  gulp.src(".")
    .pipe(webserver({
      livereload: false,
      fallback: "index.html"
    }));
});