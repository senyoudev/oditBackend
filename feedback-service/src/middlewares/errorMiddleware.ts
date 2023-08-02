import { IErrorResponse } from '../interfaces/ErrorResponse';
import { Request, Response, NextFunction } from 'express';
import logger from '../utils/logger';
import { ValidationError } from '../utils/ValidationError';

export default function errorHandler(
  err: Error,
  req: Request,
  res: Response<IErrorResponse>,
  next: NextFunction
) {


  if (err instanceof ValidationError) {
    res.json({
      errors: [
        {
          title: 'Validation Error',
          status: res.statusCode || 500,
          detail: err.message,
          message:err.message,
          meta: { errors: err.errors },
        },
      ],
    });
  } else {
    res.json({
      errors: [
        {
          title: err.name || 'Internal Server Error',
          status: res.statusCode || 500,
          detail: err.message,
          message: err.message,
          meta:
            process.env.NODE_ENV === 'production'
              ? undefined
              : { stack: err.stack },
        },
      ],
    });

    logger.error({
      message: err.message,
      stack: err.stack,
      status:res.statusCode || 500,
      timestamp: Date.now(),
    });
  }
}