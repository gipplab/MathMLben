module.exports = {
  /**
   * Application configuration section
   * http://pm2.keymetrics.io/docs/usage/application-declaration/
   */
  apps : [

    // First application
    {
      name      : 'gouldi',
      script    : '/srv/gouldi/current/gouldi.js',
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
      path : '/srv/gouldi',
      "post-deploy" : "npm i; pm2 startOrRestart ecosystem.config.js --env production"
    }
  }
};