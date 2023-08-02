export interface IErrorResponse {
  errors: {
    title: string;
    status: number;
    detail: string;
    message:string;
    meta?: { [key: string]: any };
  }[];
}