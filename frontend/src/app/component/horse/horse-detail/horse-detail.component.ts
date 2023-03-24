import {Component, OnInit} from '@angular/core';
import {NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Horse} from 'src/app/dto/horse';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {OwnerService} from 'src/app/service/owner.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-horse-detail',
  templateUrl: './horse-detail.component.html',
  styleUrls: ['./horse-detail.component.scss']
})
export class HorseDetailComponent implements OnInit {

  horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };

  constructor(
    private ownerService: OwnerService,
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
        },
        error: (errorResponse: HttpErrorResponse) => {
          console.error('ERROR: No horse found with id:', horseId);
          this.notification.error(`Could not find horse to edit`);
          this.routToRoot();
        }
      });

    });
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  private routToRoot(): void {
    this.router.navigate(['/horses']);
  }
}
