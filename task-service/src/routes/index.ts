import { Router } from 'express';
import taskRoutes from './taskRoutes';

const baseRoutes = Router();

baseRoutes.use('/tasks',taskRoutes );


export default baseRoutes;