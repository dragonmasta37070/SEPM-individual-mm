import {Component, OnInit} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Owner} from 'src/app/dto/owner';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {OwnerService} from 'src/app/service/owner.service';
import {HttpErrorResponse} from '@angular/common/http';


export enum HorseCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
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

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Update Horse';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }


  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  motherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.horseService.searchByNameAndSex(input, 5, Sex.female);

  fatherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.horseService.searchByNameAndSex(input, 5, Sex.male);

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;

      if (this.mode === HorseCreateEditMode.edit) {
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
      }
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

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public formatParentName(parent: Horse | null | undefined): string {
    return (parent == null)
      ? ''
      : `${parent.name}`;
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      if (this.horse.description === '') {
        delete this.horse.description;
      }
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.horseService.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          observable = this.horseService.update(this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {
          this.notification.error(`Error while creating ${this.horse.name}: ${error.error.errors}`);
          console.error('Error creating horse', error);
        }
      });
    }
  }

  public onDelete(): void {
    if (!this.modeIsCreate && this.horse.id != null) {
      this.horseService.delete(this.horse.id).subscribe({
        next: () => {
          this.notification.success(`Horse ${this.horse.name} was deleted successfully`);
          this.routToRoot();
        },
        error: (errorResponse: HttpErrorResponse) => {
          this.notification.error(`Error while deleting horse: ${errorResponse.error.errors}`)
          console.error(`ERROR while deleting horse with id: ${this.horse.id}`, errorResponse.error.errors)
        }
      });
    }
  }

  private routToRoot(): void {
    this.router.navigate(['/horses']);
  }

}
