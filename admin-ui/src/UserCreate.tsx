import { Create, SimpleForm, BooleanInput, TextInput, required, email } from 'react-admin';

export const UserCreate = () => (
  <Create>
    <SimpleForm>
        <TextInput source="email" validate={[email(), required()]} />
        <TextInput source="fullName" validate={[required()]} />
        <TextInput source="address" validate={[required()]} />
        <BooleanInput source="active"  defaultValue="1" validate={[required()]} />
    </SimpleForm>
  </Create>
);
