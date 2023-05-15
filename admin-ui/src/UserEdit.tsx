import { Edit, SimpleForm, BooleanInput, TextInput, required, email } from 'react-admin';

export const UserEdit = () => (
  <Edit>
    <SimpleForm>
        <TextInput source="email" validate={[email(), required()]} />
        <TextInput source="fullName" validate={[required()]} />
        <TextInput source="address" validate={[required()]} />
        <BooleanInput source="active" validate={[required()]} />
    </SimpleForm>
  </Edit>
);
