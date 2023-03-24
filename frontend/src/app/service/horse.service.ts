import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse, HorseSearch} from '../dto/horse';
import {Sex} from "../dto/sex";

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) {
  }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  searchHorses(data: HorseSearch = {}): Observable<Horse[]> {
    let params = new HttpParams();
    // if (data.name !== undefined) {
    //   params = params.append("name", data.name);
    // }

    Object.keys(data).forEach(
      key => {
        const objectKey = key as keyof HorseSearch;
        const objectValue = data[objectKey];
        if (objectValue !== undefined) {
          params = params.append(objectKey, objectValue);
        }
      }
    );

    return this.http.get<Horse[]>(baseUri, {params});
  }


  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  /**
   * Update parameters of an existing horese
   *
   * @param horse data to update
   * @return an Observable of the updated horse
   */
  update(horse: Horse): Observable<Horse> {
    return this.http.put<Horse>(
      `${baseUri}/${horse.id}`,
      horse
    );
  }

  /**
   * Get an existing horse by its id
   *
   * @param id the id by witch the horse is referenced
   * @return an Observable of the horse
   */
  get(id: number): Observable<Horse> {
    return this.http.get<Horse>(
      `${baseUri}/${id}`
    );
  }

  /**
   * Delete a horse by its id
   *
   * @param id he id by witch the horse is referenced
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${baseUri}/${id}`
    );
  }

  /**
   * Search horses by there name and sex
   * @param name name to search for
   * @param limitTo limits the number of returned horses
   * @param sex sex to search for
   *
   * @return horses with matching values
   */
  public searchByNameAndSex(name: string, limitTo: number, sex: Sex): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('maxAmount', limitTo)
      .set('sex', sex);
    //TODO this will be implemented later
    return this.http.get<Horse[]>(baseUri, {params});
  }
}
