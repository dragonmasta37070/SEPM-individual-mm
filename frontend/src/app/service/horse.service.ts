import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Horse} from '../dto/horse';
import {Owner} from "../dto/owner";
import {Sex} from "../dto/sex";

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<Horse[]> {
    return this.http.get<Horse[]>(baseUri);
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
   * Gat an existing horse by its
   *
   * @param id the id by witch the horse is referenced
   * @return an Observable of the horse
   */
  get(id: number): Observable<Horse> {
    return this.http.get<Horse>(
      `${baseUri}/${id}`
    );
  }

  //TODO COMMENT!
  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${baseUri}/${id}`
    );
  }

  public searchByNameAndSex(name: string, limitTo: number, sex: Sex): Observable<Horse[]> {
    const params = new HttpParams()
      .set('name', name)
      .set('maxAmount', limitTo)
      .set('sex', sex);
    //TODO this will be implemented later
    return this.http.get<Horse[]>(baseUri, { params });
  }
}
