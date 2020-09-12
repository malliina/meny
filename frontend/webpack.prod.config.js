const BaseWebpack = require('./webpack.base.config');
const Merge = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = Merge(BaseWebpack, {
  mode: 'production',
  output: {
    filename: '[name].[chunkhash].js'
  },
  plugins: [
    new MiniCssExtractPlugin({filename: '[name].[contenthash].css'})
  ]
});
