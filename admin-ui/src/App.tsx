// in src/App.tsx
import { Admin, Resource } from "react-admin";
import jsonServerProvider from "ra-data-json-server";
import { UserCreate } from './UserCreate';
import { UserEdit } from './UserEdit';
import { UserList } from './UserList';
import { UserEventList } from './UserEventList';

const dataProvider = jsonServerProvider('http://localhost:8060');

const App = () => (
 <Admin dataProvider={dataProvider}>
   <Resource name="user/api/users" list={UserList} create={UserCreate} edit={UserEdit} />
   <Resource name="event/api/events/all" list={UserEventList} />
 </Admin>
);

export default App;
// http://localhost:8060/user/api/users
// http://localhost:8060/user/api/users?_end=10&_order=ASC&_sort=id&_start=0
