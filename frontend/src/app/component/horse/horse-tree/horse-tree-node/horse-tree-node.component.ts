import {Component, Input} from '@angular/core';
import {HorseTree} from "../../../../dto/horse";
import {HorseTreeComponent} from "../horse-tree.component";

@Component({
  selector: 'app-horse-tree-node',
  templateUrl: './horse-tree-node.component.html',
  styleUrls: ['./horse-tree-node.component.scss']
})
export class HorseTreeNodeComponent {

  collapsParents?: boolean;
  constructor(
    private treeService: HorseTreeComponent,
    ) {
    this.collapsParents = false;
  }

  @Input()
  horse?: HorseTree;

  delete(id: number) {
    this.treeService.delete(id);
  }

  toggleParents(){
    this.collapsParents == false ? this.collapsParents = true : this.collapsParents = false;
  }
}

