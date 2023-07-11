import { createLogger, transports, format } from 'winston';

const logger = createLogger({
  level: 'info',
  format: format.json(),
  defaultMeta: { service: 'my-service' },
  transports: [
    new transports.File({ filename: 'error.log', level: 'error' }),
    new transports.Console({ level: 'debug' }),
  ],
});


export default logger