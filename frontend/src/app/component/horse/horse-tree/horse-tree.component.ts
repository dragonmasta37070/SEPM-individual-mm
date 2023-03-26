import {Component, OnInit} from '@angular/core';
import {Horse, HorseTree} from "../../../dto/horse";
import {Sex} from "../../../dto/sex";
import {HorseService} from "../../../service/horse.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {Observable} from "rxjs";

@Component({
  selector: 'app-horse-tree',
  templateUrl: './horse-tree.component.html',
  styleUrls: ['./horse-tree.component.scss']
})
export class HorseTreeComponent implements OnInit {
  public horseTree$?: Observable<HorseTree>
  public generations = 5;
  public id?: number
  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };

  constructor(
    private horseService: HorseService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      const horseId = Number(this.route.snapshot.paramMap.get('id'));

      if (Number.isNaN(horseId)) {
        console.error('horse id is not a number', horseId);
        this.notification.error('horse id is not a number');
        this.router.navigate(['/horses']);
      }

      this.horseService.get(horseId).subscribe({
        next: horse => {
          this.horse = horse;
          this.id = horse.id;
          this.loadTree();
        },
        error: () => {
          console.error('ERROR: No horse found with id:', horseId);
          this.notification.error(`Could not find horse to edit`);
          this.router.navigate(['/horses']);
        }
      });
    });
    this.loadTree();
  }

  loadTree() {
    if(Number.isNaN(this.id) || this.id === undefined) {
      throw new Error('invalid id');
    }
    this.horseTree$ = this.horseService.getTree(this.id, this.generations)
  }
}
