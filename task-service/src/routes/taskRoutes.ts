import express from 'express';
import { assignTask, createTask, deleteTask, getSectionTasks, getTaskById, markTaskDone, updateTask } from '../controllers/taskController'


const router = express.Router();

router.post('/',createTask);
router.put('/:taskId',updateTask);
router.get('/:taskId',getTaskById);
router.get('/:sectionId',getSectionTasks);
router.delete('/:taskId',deleteTask);
router.post('/:taskId/assign',assignTask);
router.put('/:taskId/done',markTaskDone);

export default router;
