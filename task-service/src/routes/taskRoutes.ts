import express from 'express';


const router = express.Router();

router.post('/tasks');
router.put('/tasks/:taskId');
router.get('/tasks/:taskId');
router.get('/sections/:sectionId/tasks');
router.delete('/tasks/:taskId');
router.post('/tasks/:taskId/assign');
router.put('/tasks/:taskId/done');

export default router;
