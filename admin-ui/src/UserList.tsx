import { BooleanField, Datagrid, EmailField, List, TextField } from 'react-admin';

export const UserList = () => (
    <List>
        <Datagrid rowClick="edit">
            <TextField source="id" />
            <EmailField source="email" />
            <TextField source="fullName" />
            <TextField source="address" />
            <BooleanField source="active" />
        </Datagrid>
    </List>
);
