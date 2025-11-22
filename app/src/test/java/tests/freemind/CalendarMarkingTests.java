package tests.freemind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Set;

import org.junit.jupiter.api.Test;

import accessories.plugins.time.CalendarMarkingEvaluator;
import freemind.common.XmlBindingTools;
import freemind.controller.actions.generated.instance.CalendarMarking;
import freemind.controller.actions.generated.instance.CalendarMarkings;

public class CalendarMarkingTests extends FreeMindTestBase {
	private long toEpochMillisecond(String date) {
		String[] dmy = date.split("\\.");
		return ZonedDateTime.of(Integer.parseInt(dmy[2]), Integer.parseInt(dmy[1]),
				Integer.parseInt(dmy[0]), 0, 0, 0, 0, ZoneId.systemDefault()).toEpochSecond()
				* 1000;
	}

	@Test
	public void testCalendarMarkingEmpty() throws Exception {
		CalendarMarkings result =
				(CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings/>");
		assertEquals(0, result.sizeCalendarMarkingList());
	}

	@Test
	public void testCalendarMarkingSingle() throws Exception {
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla' color='#ff69b4' start_date='1437213300000' repeat_type='never'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingDouble() throws Exception {
		long otherTime = toEpochMillisecond("1.1.1970");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla' color='#ff69b4' start_date='1437213300000' repeat_type='never'/>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + otherTime
						+ "' repeat_type='never'/>" + "</calendar_markings>");
		assertEquals(2, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.setTimeInMillis(otherTime);
		CalendarMarking marked = ev.isMarked(cal);
		assertNotNull(marked);
		assertEquals("bla2", marked.getName());
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatWeekly() throws Exception {
		long startTime = toEpochMillisecond("5.7.2015");
		long endTime = toEpochMillisecond("19.7.2015");
		String inputString = "<calendar_markings>"
				+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
				+ "' end_date='" + endTime + "' "
				+ "repeat_type='weekly' repeat_each_n_occurence='1' first_occurence='0'/>"
				+ "</calendar_markings>";
		System.out.println(inputString);
		CalendarMarkings result =
				(CalendarMarkings) XmlBindingTools.getInstance().unMarshall(inputString);

		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, -1);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNotNull(ev.isMarked(cal));
		cal.setTimeInMillis(endTime);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatBeWeekly() throws Exception {
		long startTime = toEpochMillisecond("5.7.2015");
		long endTime = toEpochMillisecond("19.7.2015");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
						+ "' end_date='" + endTime + "' "
						+ "repeat_type='weekly' repeat_each_n_occurence='2' first_occurence='0'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, -1);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatDaily() throws Exception {
		long startTime = toEpochMillisecond("5.7.2015");
		long endTime = toEpochMillisecond("19.7.2015");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
						+ "' end_date='" + endTime + "' "
						+ "repeat_type='daily' repeat_each_n_occurence='1' first_occurence='0'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNotNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatMonthly() throws Exception {
		long startTime = toEpochMillisecond("5.7.2015");
		long endTime = toEpochMillisecond("5.8.2015");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
						+ "' end_date='" + endTime + "' "
						+ "repeat_type='monthly' repeat_each_n_occurence='1' first_occurence='0'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.MONTH, 1);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.MONTH, 1);
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatYearly() throws Exception {
		long startTime = toEpochMillisecond("5.7.2015");
		long endTime = toEpochMillisecond("5.7.2016");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
						+ "' end_date='" + endTime + "' "
						+ "repeat_type='yearly' repeat_each_n_occurence='1' first_occurence='0'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.YEAR, 1);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatYearlyEveryNthDay() throws Exception {
		long startTime = toEpochMillisecond("30.1.2015");
		long endTime = toEpochMillisecond("29.2.2016");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
						+ "' end_date='" + endTime + "' "
						+ "repeat_type='yearly_every_nth_day' repeat_each_n_occurence='30' first_occurence='30'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		ev.print();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(startTime);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 29);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 30 * 11);
		assertNull(ev.isMarked(cal));
		long nextYearDate = toEpochMillisecond("30.1.2016");
		cal.setTimeInMillis(nextYearDate);
		assertNotNull(ev.isMarked(cal));
		cal.setTimeInMillis(endTime);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 30);
		assertNull(ev.isMarked(cal));
	}

	/**
	 * Here, the start date is set after the first occurrence. The start date should now be the
	 * first to be marked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCalendarMarkingRepeatYearlyEveryNthDayStartAfterFirstOccurrence()
			throws Exception {
		long startTime = toEpochMillisecond("27.10.2015");
		long endTime = toEpochMillisecond("29.2.2016");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
						+ "' end_date='" + endTime + "' "
						+ "repeat_type='yearly_every_nth_day' repeat_each_n_occurence='30' first_occurence='30'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		// ev.print();
		Calendar cal = Calendar.getInstance();
		long nextYearDate = toEpochMillisecond("30.1.2015");
		cal.setTimeInMillis(nextYearDate);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 30);
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatWeeklyEveryNthDay() throws Exception {
		long startTime = toEpochMillisecond("1.1.2015");
		long endTime = toEpochMillisecond("1.3.2015");
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance()
				.unMarshall("<calendar_markings>"
						+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
						+ "' end_date='" + endTime + "' "
						+ "repeat_type='weekly_every_nth_day' repeat_each_n_occurence='2' first_occurence='5'/>"
						+ "</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		// ev.print();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(toEpochMillisecond("3.1.2015"));
		assertNotNull(ev.isMarked(cal));
		cal.setTimeInMillis(toEpochMillisecond("8.1.2015"));
		assertNotNull(ev.isMarked(cal));
		cal.setTimeInMillis(toEpochMillisecond("9.1.2015"));
		assertNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatYearlyEveryNthWeek() throws Exception {
		long startTime = toEpochMillisecond("9.1.2015");
		long endTime = toEpochMillisecond("13.2.2016");
		String inputString = "<calendar_markings>"
				+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
				+ "' end_date='" + endTime + "' "
				+ "repeat_type='yearly_every_nth_week' repeat_each_n_occurence='2' first_occurence='2'/>"
				+ "</calendar_markings>";
		System.out.println(inputString);
		CalendarMarkings result =
				(CalendarMarkings) XmlBindingTools.getInstance().unMarshall(inputString);
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		ev.print();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(toEpochMillisecond("5.2.2016"));
		assertNotNull(ev.isMarked(cal));
	}

	@Test
	public void testCalendarMarkingRepeatYearlyEveryNthWeekStrangeDates() throws Exception {
		long startTime = toEpochMillisecond("9.1.2015");
		long endTime = toEpochMillisecond("9.1.2016");
		String inputString = "<calendar_markings>"
				+ "  <calendar_marking name='bla2' color='#ff69b5' start_date='" + startTime
				+ "' end_date='" + endTime + "' "
				+ "repeat_type='yearly_every_nth_week' repeat_each_n_occurence='1' first_occurence='0'/>"
				+ "</calendar_markings>";
		System.out.println(inputString);
		CalendarMarkings result =
				(CalendarMarkings) XmlBindingTools.getInstance().unMarshall(inputString);
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		ev.print();
		Set<Calendar> nEntries = ev.getAtLeastTheFirstNEntries(10);
		assertTrue(nEntries.size() >= 10);
	}
}
