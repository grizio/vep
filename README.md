# Voir & Entendre

## Getting start - dev

```bash
# First tab
$ npm i
$ npm start

# second tab, manual restart required
$ sbt run
```

## Goto prod

Create another local environment to avoid conflict.
The first time, please check how to setup git with clever-cloud.

See https://www.clever-cloud.com/doc/clever-cloud-overview/add-application/

### Build

```bash
$ npm i
$ npm run build
# Change build.sbt to update version
$ git add *
$ git commit -m "v<version>"
$ git push
```

### Send to Clever Cloud

```bash
$ git push clever v2017:master
```