import {Owner} from './owner';
import {Sex} from './sex';

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  owner?: Owner;
  father?: Horse;
  mother?: Horse;
}


export interface HorseSearch {
  name?: string;
  description?: string;
  bornBefore?: string;
  sex?: Sex;
  owner?: string;
}

export interface HorseTree {
  id?: number;
  name: string;
  dateOfBirth: Date;
  sex: Sex;
  father?: Horse;
  mother?: Horse;
}
