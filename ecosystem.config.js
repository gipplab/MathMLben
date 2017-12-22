module.exports = {
  /**
   * Application configuration section
   * http://pm2.keymetrics.io/docs/usage/application-declaration/
   */
  apps : [

    // First application
    {
      name      : 'gouldi',
      script    : 'gouldi.js',
      env_production : {
        GOULDI_PORT: 34512
      }
    },
  ],

  /**
   * Deployment section
   * http://pm2.keymetrics.io/docs/usage/deployment/
   */
  deploy : {
    production : {
      user : 'gouldi',
      host : 'localhost',
      ref  : 'origin/master',
      repo : 'https://github.com/ag-gipp/GoUldI',
      path : '/srv/gouldi'
    }
  }
};