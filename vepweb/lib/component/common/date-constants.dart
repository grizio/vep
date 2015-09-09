library vep.component.common.constants;

const typeDate = 'date';
const typeTime = 'time';
const typeDatetime = 'datetime';

const allTypes = const[typeDate, typeTime, typeDatetime];

/// Date formats for display.
const displayFormatsDT = const<String, String>{
  typeDate: 'dd/MM/yyyy',
  typeTime: 'HH:mm',
  typeDatetime: 'dd/MM/yyyy HH:mm'
};

/// Date formats for inner values (with datetimepicker)
const displayFormatsDP = const<String, String>{
  typeDate: 'd/m/Y',
  typeTime: 'H:i',
  typeDatetime: 'd/m/Y H:i'
};

/// Date format for outer attributes ([min] and [max]).
const devFormatDP = const<String, String>{
  typeDate: 'Y-m-d',
  typeTime: 'H:i',
  typeDatetime: 'Y-m-d'
};

/// Date format for outer attributes ([min] and [max]).
const devFormatDT = const<String, String>{
  typeDate: 'yyyy-MM-dd',
  typeTime: 'HH:mm',
  typeDatetime: 'yyyy-MM-dd'
};