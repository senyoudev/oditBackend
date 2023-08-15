import { Eureka } from 'eureka-js-client';

const port = process.env.PORT || 5000;
const eurekaPort = process.env.EUREKA_PORT || 8761;
const hostName = process.env.HOSTNAME || 'localhost';
const eurekaHost =
  process.env.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE || "localhost";
const ipAddr = '127.0.0.1';

const eurekaClient = new Eureka({
  instance: {
    app: 'Upload',
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: port as number,
      '@enabled': true,
    },
    vipAddress: 'Upload',
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
    preferIpAddress:true
  },
});

export default eurekaClient as any;
