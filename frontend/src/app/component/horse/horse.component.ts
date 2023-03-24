import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseSearch} from '../../dto/horse';
import {Owner} from '../../dto/owner';
import {HttpErrorResponse} from "@angular/common/http";
import {NgForm} from "@angular/forms";
import {debounceTime, Subscription} from "rxjs";

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('form', {static: true}) ngForm?: NgForm;

  search: HorseSearch = {};
  filterUpdated?: Subscription;
  horses: Horse[] = [];
  bannerError: string | null = null;

  constructor(
    private service: HorseService,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.searchHorses(this.search)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  delete(id: number): void {
    this.service.delete(id).subscribe({
      next: () => {
        this.reloadHorses();
        this.notification.info('Horse was deleted successfully');
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.notification.error(`Error while deleting horse: ${errorResponse.error.errors}`)
        console.error(`ERROR while deleting horse with id: ${id}`, errorResponse.error.errors)
      }
    });
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  ngAfterViewInit(): void {
    this.filterUpdated = this.ngForm?.valueChanges?.pipe(debounceTime(150))
      .subscribe(this.reloadHorses.bind(this))

  }

  ngOnDestroy(): void {
    this.filterUpdated?.unsubscribe();
  }

}
