import {resolve} from "path";

export default function (config, env, helpers) {
  config.module.loaders.push({
    enforce: 'pre',
    test: /\.tsx?$/,
    use: [
      {
        loader: resolve(__dirname, 'node_modules/preact-cli/lib/lib/webpack/npm-install-loader.js'),
        options: {
          modules: ['typescript', 'ts-loader'],
          save: true
        }
      },
      {
        loader: 'ts-loader',
        options: {
          sourceMap: true,
          useBabel: true,
          useCache: true
        }
      }
    ]
  })
  config.output.filename = "[name].[chunkhash:5].js"
}