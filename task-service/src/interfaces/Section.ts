import { ITask } from "./Task";

export interface ISection {
  roomId: string;
  name: string;
  tasks: ITask[];
}