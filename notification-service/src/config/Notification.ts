import { Entity, PrimaryGeneratedColumn, Column } from "typeorm";

@Entity()
export class Notification {
  @PrimaryGeneratedColumn()
  id:any;

  @Column({ type: "jsonb" })
  from:any;

  @Column({ type: "jsonb" })
  to:any;

  @Column({ type: "jsonb" })
  type:any;
}
