module.exports = {
  content: [
    './src/main/scala/**/*.scala',
    './src/main/js/**/*.js',
    "./node_modules/flowbite/**/*.js"
  ],
  theme: {
    extend: {},
  },
  darkMode: 'selector',
  plugins: [
    require('flowbite/plugin')
  ],
}

