import { Eureka } from 'eureka-js-client';

const port = process.env.PORT || 4000;
const eurekaPort = process.env.EUREKA_PORT || 8761;
const hostName = process.env.HOSTNAME || 'localhost';
const eurekaHost =
  process.env.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE || "eureka-server";
const ipAddr = '127.0.0.1';

const eurekaClient = new Eureka({
  instance: {
    app: 'upload',
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: 80,
      '@enabled': true,
    },
    vipAddress: 'upload',
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: eurekaHost,
    port: eurekaPort as number,
    servicePath: '/eureka/apps/',
    maxRetries: 10,
    requestRetryDelay: 2000,
    preferIpAddress: true,
  },
});

export default eurekaClient as any;
