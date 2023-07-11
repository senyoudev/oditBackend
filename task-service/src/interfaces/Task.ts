export interface ITask {
  sectionId: string;
  name: string;
  description: string;
  startDate: Date;
  deadline: Date;
  isDone:Boolean;
  assignedMembers: number[];
}