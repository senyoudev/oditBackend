import { Eureka } from 'eureka-js-client';

const port = process.env.PORT || 4000;
const eurekaPort = process.env.EUREKA_PORT || 8761;
const hostName = process.env.HOSTNAME || 'localhost';
const eurekaHost =
  process.env.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE || '127.0.0.1';
const ipAddr = '127.0.0.1';

const eurekaClient = new Eureka({
  instance: {
    app: 'Notification',
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: port as number,
      '@enabled': true,
    },
    vipAddress: 'Notification',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: eurekaHost,
    port: eurekaPort as number,
    servicePath: '/eureka/apps/',
    maxRetries: 1,
    requestRetryDelay: 2000,
  },
});

export default eurekaClient as any;