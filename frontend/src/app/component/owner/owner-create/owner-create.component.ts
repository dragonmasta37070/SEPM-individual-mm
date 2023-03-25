import {Component} from '@angular/core';
import {OwnerService} from "../../../service/owner.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {Owner} from "../../../dto/owner";

@Component({
  selector: 'app-owner-create',
  templateUrl: './owner-create.component.html',
  styleUrls: ['./owner-create.component.scss']
})
export class OwnerCreateComponent {

  owner: Owner = {
    firstName: '',
    lastName: '',
    email: '',
  }

  constructor(
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public onSubmit(form: NgForm): void {
    let observable: Observable<Owner>;
    observable = this.ownerService.create(this.owner);

    observable.subscribe({
      next: () => {
        this.notification.success(`Owner ${this.owner.firstName} ${this.owner.firstName} created successfully`);
        this.router.navigate(['/owners']);
      },
      error: error => {
        this.notification.error(`Error while creating
          ${this.owner.firstName} ${this.owner.firstName}: ${error.error.errors}`);
        console.error('Error creating owner', error);
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
}
