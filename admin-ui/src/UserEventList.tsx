import { Datagrid, DateField, List, ReferenceField, TextField } from 'react-admin';
import { useParams } from 'react-router-dom';

export const UserEventList = () => {

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const { userId } = useParams(); // eslint-disable-line @typescript-eslint/no-unused-vars
    return (
       <List>
           <Datagrid>
               <TextField source="id" />
               <ReferenceField source="userId" reference="user/api/users/:userId" />
               <DateField source="datetime" />
               <TextField source="type" />
           </Datagrid>
       </List>
    );
}
