const path = require('path');
const webpack = require('webpack');

module.exports = {
  entry: {
    index: './src/index.ts',
    preload: './src/preload.ts',
  },
  output: {
    filename: './[name].js',
    path: path.resolve(__dirname, 'electron'),
  },
  mode: 'production',
  target: 'electron-main',
  plugins: [
    new webpack.DefinePlugin({
      'process.env.DSPRING_PROFILES_ACTIVE': JSON.stringify(process.env.DSPRING_PROFILES_ACTIVE),
      'process.env.APP_NAME': JSON.stringify(process.env.APP_NAME),
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV),
      'process.env.DNETWORK_STATUS': JSON.stringify(process.env.DNETWORK_STATUS),
    }),
  ],
  module: {
    rules: [
      {
        test: /\.(js|ts|tsx)$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env', '@babel/preset-typescript'],
          },
        },
      },
    ],
  },
  resolve: {
    extensions: ['.ts', '.tsx', '.js'],
  },
  resolveLoader: {
    modules: ['node_modules'],
  },
};
