import express from 'express';
import { assignTask, createTask, deleteTask, getTaskById, markTaskDone, updateTask } from '../controllers/taskController'


const router = express.Router();

router.get('/:taskId',getTaskById);
router.post('/',createTask);
router.put('/:taskId',updateTask);
router.delete('/:taskId',deleteTask);
router.put('/:taskId/assign',assignTask);
router.put('/:taskId/done',markTaskDone);

export default router;
