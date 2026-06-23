import pluginVue from 'eslint-plugin-vue';

export default [
  {
    ignores: ['dist/**'],
  },
  ...pluginVue.configs['flat/recommended'],
  {
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
    },
    rules: {
      'vue/attributes-order': 'off',
      'vue/html-closing-bracket-newline': 'off',
      'vue/html-indent': 'off',
      'vue/html-self-closing': 'off',
      'vue/max-attributes-per-line': 'off',
      'vue/multi-word-component-names': 'off',
      'vue/script-indent': 'off',
      'vue/singleline-html-element-content-newline': 'off',
    },
  },
];
