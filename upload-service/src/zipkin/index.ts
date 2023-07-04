//@ts-ignore
import { Tracer, ExplicitContext, BatchRecorder, jsonEncoder } from 'zipkin';
//@ts-ignore
const { HttpLogger } = require('zipkin-transport-http');
//@ts-ignore
import { expressMiddleware } from 'zipkin-instrumentation-express';


const ZIPKIN_ENDPOINT =
  process.env.ZIPKIN_ENDPOINT || 'http://localhost:9411/zipkin';

// Get ourselves a zipkin tracer
const tracer = new Tracer({
  ctxImpl: new ExplicitContext(),
  recorder: new BatchRecorder({
    logger: new HttpLogger({
      endpoint: `${ZIPKIN_ENDPOINT}/api/v2/spans`,
      jsonEncoder: jsonEncoder.JSON_V2,
    }),
  }),
  localServiceName: 'upload',
});

export default expressMiddleware({ tracer });
