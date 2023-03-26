import {Component, Input} from '@angular/core';
import {HorseTree} from "../../../../dto/horse";
import {HorseService} from "../../../../service/horse.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-horse-tree-node',
  templateUrl: './horse-tree-node.component.html',
  styleUrls: ['./horse-tree-node.component.scss']
})
export class HorseTreeNodeComponent {
  @Input()
  horse?: HorseTree;
}

