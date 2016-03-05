var gulp = require("gulp");
var watch = require('gulp-watch');
var plumber = require('gulp-plumber');
var babel = require("gulp-babel");
var webserver = require('gulp-webserver');

gulp.task("compileJs", function () {
  return gulp.src("src/**/*.es6")
    .pipe(babel())
    .pipe(gulp.dest("dist"));
});

gulp.task("js", ["compileJs"], function () {
  return gulp.src("src")
    .pipe(watch("src/**/*.es6"))
    .pipe(plumber())
    .pipe(babel())
    .pipe(gulp.dest("dist"));
});

gulp.task("compileHtml", function () {
  return gulp.src("src/**/*.html")
    .pipe(gulp.dest("dist"));
});

gulp.task("html", ["compileHtml"], function () {
  return gulp.src(".")
    .pipe(watch("src/**/*.html"))
    .pipe(plumber())
    .pipe(gulp.dest("dist"));
});

gulp.task("webserver", function () {
  gulp.src(".")
    .pipe(webserver({
      livereload: true,
      fallback: "index.html"
    }));
});